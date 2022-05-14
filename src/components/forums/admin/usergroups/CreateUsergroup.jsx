import React, { useEffect, useState } from 'react';
import useDyanmicRefs from '../../../../utils/hooks/use-dynamic-refs';

import Input from '../../../utils/Input';

const CreateUsergroup = React.forwardRef(({ create }, ref) => {
    let [ name, setName ] = useState(ref.current.name || '');
    let [ rights, setRights ] = useState(ref.current.rights || 0);
    let [ colour ] = useState(ref.current.colour || '');
    let [ title, setTitle ] = useState(ref.current.title || '');
    let [ imageBefore, setImageBefore ] = useState(ref.current.imageBefore || '');
    let [ imageAfter, setImageAfter ] = useState(ref.current.imageAfter || '');

    let [ getRef ] = useDyanmicRefs([ 'rights', 'colour', 'title', 'imageBefore', 'imageAfter' ]);

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
                <Input 
                    className='m-auto'
                    placeholder='Name' 
                    value={name} 
                    setState={setName} 
                    next={getRef('rights')}
                />
                <p>Rights</p>
                <Input 
                    ref={getRef('rights')}
                    className='m-auto'
                    type='number' 
                    placeholder='Rights' 
                    value={rights} 
                    setState={setRights} 
                    // next={getRef('colour')}
                />
                <p>Colour</p>
                <p>TODO</p>
                <p>Title</p>
                <Input 
                    ref={getRef('title')}
                    className='m-auto'
                    placeholder='Title' 
                    value={title} 
                    setState={setTitle} 
                    next={getRef('imageBefore')}
                />
                <p>Image Before</p>
                <Input 
                    ref={getRef('imageBefore')}
                    className='m-auto'
                    placeholder='Image Before' 
                    value={imageBefore} 
                    setState={setImageBefore} 
                    next={getRef('imageAfter')}
                />
                <p>Image After</p>
                <Input 
                    ref={getRef('imageAfter')}
                    className='m-auto'
                    placeholder='Image After' 
                    value={imageAfter} 
                    setState={setImageAfter}
                    onEnter={create}
                />
            </div>
        </div>
    )
});

export default CreateUsergroup;
