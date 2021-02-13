function createChatObject(sendMessage, rights) {
    return {

        messages: [],
        delay: 0,

        submitMessage: function() {
            let message = $('#neko-chat input').val();

            if (message.length < 4 || message.length > 100) {
                sendAlert('Messages must be between 4 and 100 characters.');
                return false;
            }

            if (this.delay > Date.now()) {
                sendAlert('You are sending chat messages to fast. Please wait a second.');
                return false;
            }

            message = censorChatMessage(message, rights);

            $('#neko-chat input').val('');

            sendMessage('chat/message', { content: message });
            this.delay = Date.now() + 500;
        },

        receiveMessages: function(data) {
            if (!data.messages) {
                $('#chat-container').html('');
                return false;
            }
            for (message of data.messages)
                this.appendMessage(message.id, message.author, message.content, message.stamp);
        },

        receiveMessage: function(data) {
            let id = data.message.id;
            let username = data.message.author;
            let content = data.message.content;
            let stamp = data.message.stamp;

            this.appendMessage(id, username, content, stamp);

        },

        appendMessage(id, username, content, stamp) {
            this.messages.push({
                id,
                username,
                content,
                stamp
            });

            if (this.messages.length > 200)
                this.messages.pop();

            post('/neko/chat', { messages: JSON.stringify(this.messages) }, ret => {

                $('#chat-container').html(ret.html);

                $('#chat-container').animate({ scrollTop: $('#chat-container')[0].scrollHeight }, 200);

                if (window.inFocus === false) {

                    if ($('#neko-chat').css('display') == 'none') return;

                    if (localStorage.getItem('notification-sound') == null || localStorage.getItem('notification-sound') == 0) {
                        new Audio('/sounds/notification.mp3').play().catch(console.error);
                    } else console.log(localStorage.getItem('notification-sound'));
                    $('title').html('New message!');
                    window.newMessage = true;
                }

            });
        },

        removeMessage: function(data) {
            let id = data.id;
            $('.chat[data-id=' + id + ']').remove();
        },

        sendRemoveMessage: function() {
            if (rights < 1) return false;
            let id = $(this).closest('.chat').data('id');
            console.log(id);
            console.log($(this).closest('.chat'));
            if (!id) return false;
            sendMessage('chat/remove', { id });
            sendAlert('Attempting to remove message!');
        },

        load: function() {
            $('#neko-chat span').click(this.submitMessage);
            $('#neko-chat input').keydown((event) => {
                if (event.which == 13) {
                    this.submitMessage();
                    return false;
                }
            });

            $(document).on('click', '.chat-remove', this.sendRemoveMessage);
            $(document).on('mouseover', '.chat', function() {
                if (rights < 2) return false;
                $(this).find('.chat-remove').css('display', 'block');
            });

            $(document).on('mouseleave', '.chat', function() {
                if (rights < 2) return false;
                $(this).find('.chat-remove').css('display', 'none');
            });
        }

    };
}