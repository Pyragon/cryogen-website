var rights = "!{user.getRights()}"
$(document).ready(() => {

  function submitComment() {
    var input = $('#comment-input')
    var comment = input.val()
    var listId = $('#comments').data('listid')
    var rightsReq = $('#comments').data('rights')
    $.post('/comments', { action:'submit', comment:comment, id:listId }, (ret) => {
      var data = getJSON(ret)
      if(data == null) return false
      $('#comments').html(data.html)
      sendAlert('Comment has been successfully submitted.')
      input.val('')
    })
  }

  function viewRemoveComment() {
    var id = $(this).closest('.comment-box').data('id')
    var listId = $('#comments').data('listid')
    comment_n = noty({
      text: 'Are you sure you wish to delete this comment?',
      type: 'confirm',
      layout: 'topRight',
      dismissQueue: true,
      theme: 'cryogen',
      buttons: [
        {
          addClass: 'btn btn-success', text: 'Remove', onClick: function($noty) {
            if(removeComment(id, listId)) {
              closeNoty($noty)
              comment_n.close()
              comment_n = null
            }
          }
        },
        {
          addClass: 'btn btn-danger', text: 'Cancel', onClick: function($noty) {
            $noty.close()
            comment_n = null
          }
        }
      ]
    })
  }

  function removeComment(id, listId) {
    $.post('/comments', { action:'remove', id:id, listid:listId }, (ret) => {
      var data = getJSON(ret)
      if(data == null) return false
      $('#comments').html(data.html)
      sendAlert('Comment has been successfully removed.')
      return true
    })
    return true
  }

  $(document).on('click', '#comment-btn', submitComment)
  $(document).on('click', '.remove-comment', viewRemoveComment)

})
