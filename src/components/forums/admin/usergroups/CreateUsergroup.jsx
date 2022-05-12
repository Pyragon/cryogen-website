import React, { useEffect, useState } from 'react';

import Input from '../../../utils/Input';

const CreateUsergroup = React.forwardRef((_, ref) => {
    let [ name, setName ] = useState(ref.current.name || '');
    let [ rights, setRights ] = useState(ref.current.rights || 0);
    let [ colour ] = useState(ref.current.colour || '');
    let [ title, setTitle ] = useState(ref.current.title || '');
    let [ imageBefore, setImageBefore ] = useState(ref.current.imageBefore || '');
    let [ imageAfter, setImageAfter ] = useState(ref.current.imageAfter || '');

    useEffect(() => ref.current = {
            name,
            rights,
            colour,
            title,
            imageBefore,
            imageAfter,
        }, [ name, rights, colour, title, imageBefore, imageAfter ]);

    return (
        <div className='create-usergroup-container'>
            <h1>Create Usergroup</h1>
            <div className='create-usergroup-values'>
                <p>Name</p>
                <Input placeholder='Name' value={name} setState={setName} />
                <p>Rights</p>
                <Input type='number' placeholder='Rights' value={rights} setState={setRights} />
                <p>Colour</p>
                <p>TODO</p>
                <p>Title</p>
                <Input placeholder='Title' value={title} setState={setTitle} />
                <p>Image Before</p>
                <Input placeholder='Image Before' value={imageBefore} setState={setImageBefore} />
                <p>Image After</p>
                <Input placeholder='Image After' value={imageAfter} setState={setImageAfter} />
            </div>
        </div>
    )
});

export default CreateUsergroup;
