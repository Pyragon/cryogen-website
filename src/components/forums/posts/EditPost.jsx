import React, { useState, useContext } from 'react';

import axios from '../../../utils/axios';
import { validate, validatePost } from '../../../utils/validate';

import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';

import NotificationContext from '../../../utils/contexts/NotificationContext';

export default function EditPost({ post, setEditing, setPost }) {
    let [ edit, setEdit ] = useState(post.content);

    let { sendErrorNotification } = useContext(NotificationContext);

    let savePost = async () => {

        let validateOptions = {
            edit: validatePost
        };

        let error = validate(validateOptions, { edit });
        if(error) {
            sendErrorNotification(error);
            return;
        }

        try {

            let res = await axios.put(`/forums/posts/${post._id}`, { content: edit })

            setEditing(false);
            setPost(res.data.post);

        } catch(error) {
            sendErrorNotification(error);
        }

    };

    return (
        <>
            <RichTextEditor value={edit} setState={setEdit} />
            <Button title="Save" className="save-edit-btn" onClick={savePost} />
            <Button title="Cancel" className="cancel-edit-btn" onClick={() => setEditing(false)} />
            <div style={{clear: 'both' }}/>
        </>
    )
}
