import React, { useState } from 'react';

import axios from '../../../utils/axios';

import RichTextEditor from '../../utils/editor/RichTextEditor';
import Button from '../../utils/Button';

async function savePost(post, edit, setEditing, setPost) {
    try {
        let results = await axios.post('/forums/posts/'+post._id+'/edit', 
            { content: edit });
        results = results.data;
        if(results.message) {
            console.error(results.message);
            return;
        }
        setEditing(false);
        setPost(results.post);
    } catch(err) {
        console.error(err.message || err);
    }
}

export default function EditPost({ post, setEditing, setPost }) {
    let [ edit, setEdit ] = useState(post.content);
    return (
        <>
            <RichTextEditor value={edit} setState={setEdit} />
            <Button title="Save" className="save-edit-btn" onClick={() => savePost(post, edit, setEditing, setPost)} />
            <Button title="Cancel" className="cancel-edit-btn" onClick={() => setEditing(false)} />
            <div style={{clear: 'both' }}/>
        </>
    )
}
