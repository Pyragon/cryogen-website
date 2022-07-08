import React, { useEffect, useState } from 'react';
import useDyanmicRefs from '../../../../utils/hooks/use-dynamic-refs';

import Input from '../../../utils/Input';

const CreateUsergroup = React.forwardRef(({ create }, ref) => {
    let [ name, setName ] = useState(ref.current.name || '');
    let [ rights, setRights ] = useState(ref.current.rights || 0);
    let [ colour ] = useState(ref.current.colour || '');
    let [ title, setTitle ] = useState(ref.current.title || '');
    let [ htmlBefore, setHtmlBefore ] = useState(ref.current.htmlBefore || '');
    let [ htmlAfter, setHtmlAfter ] = useState(ref.current.htmlAfter || '');

    let [ getRef ] = useDyanmicRefs([ 'rights', 'colour', 'title', 'htmlBefore', 'htmlAfter' ]);

    useEffect(() => ref.current = {
            name,
            rights,
            colour,
            title,
            htmlBefore,
            htmlAfter,
        }, [ name, rights, colour, title, htmlBefore, htmlAfter ]);

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
                    next={getRef('htmlBefore')}
                />
                <p>HTML Before</p>
                <Input 
                    ref={getRef('htmlBefore')}
                    className='m-auto'
                    placeholder='HTML Before' 
                    value={htmlBefore} 
                    setState={setHtmlBefore} 
                    next={getRef('htmlAfter')}
                />
                <p>HTML After</p>
                <Input 
                    ref={getRef('htmlAfter')}
                    className='m-auto'
                    placeholder='HTML After' 
                    value={htmlAfter} 
                    setState={setHtmlAfter}
                    onEnter={create}
                />
            </div>
        </div>
    )
});

export default CreateUsergroup;
