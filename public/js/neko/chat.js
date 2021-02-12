function createChatObject(sendMessage, getUsername) {
    return {

        messages: [],

        submitMessage: function() {
            let message = $('#neko-chat input').val();

            if (message.length < 4 || message.length > 100) {
                sendAlert('Messages must be between 4 and 100 characters.');
                return false;
            }

            $('#neko-chat input').val('');

            sendMessage('chat/message', { content: message });
        },

        receiveMessage: function(data) {
            let username = data.name; //will have to save ids to users, will change once we change how users are loaded.
            let content = data.content;

            this.messages.push({
                username,
                content,
                stamp: Date.now()
            });

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

        removeMessage: function() {

        },

        load: function() {
            $('#neko-chat span').click(this.submitMessage);
            $('#neko-chat input').keydown((event) => {
                if (event.which == 13) {
                    this.submitMessage();
                    return false;
                }
            });

            $(document).on('click', '.chat-remove', this.removeMessage);
        }

    };
}