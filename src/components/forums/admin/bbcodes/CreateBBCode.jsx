import React, { useEffect, useState } from 'react';
import useDyanmicRefs from '../../../../utils/hooks/use-dynamic-refs';

import Input from '../../../utils/Input';

const CreateBBCode = React.forwardRef(({ create }, ref) => {
    let [ name, setName ] = useState(ref.current.name || '');
    let [ description, setDescription ] = useState(ref.current.description || '');
    let [ matches, setMatches ] = useState(ref.current.matches || [ '' ]);
    let [ replace, setReplace ] = useState(ref.current.replace || '');
    let [ example, setExample ] = useState(ref.current.example || '');

    let [ getRef, setRef ] = useDyanmicRefs([ 'description', 'replace', 'example']);

    useEffect(() => ref.current = {
            name,
            description,
            matches,
            replace,
            example,
    }, [ name, description, matches, replace, example ]);

    let addMatch = () => setMatches([ ...matches, '' ]);

    return (
        <div className='create-modal-container'>
            <h1>Create BBCode</h1>
            <div className='create-modal-values'>
                <p>Name</p>
                <Input 
                    className='m-auto'
                    placeholder='Name' 
                    value={name} 
                    setState={setName} 
                    next={getRef('description')} 
                />
                <p>Description</p>
                <Input 
                    className='m-auto'
                    ref={getRef('description')} 
                    placeholder='Description' 
                    value={description} 
                    setState={setDescription} 
                    next={setRef('match_0')}
                />
                <p>
                    Matches
                    <span className='m-left-5 fa fa-plus-circle link' onClick={addMatch}/>
                </p>
                <div className='create-bbcode-matches'>
                    { matches.map((match, i) => {
                        return (
                            <Input
                                className='m-auto'
                                key={i}
                                ref={getRef('match_'+i)}
                                placeholder='Enter Match'
                                value={match}
                                setState={(val) => {
                                    let newMatches = [ ...matches ];
                                    newMatches[i] = val;
                                    setMatches(newMatches);
                                }}
                                next={i === matches.length - 1 ? getRef('replace') : getRef('match_'+(i+1))}
                            />
                        )
                    }) }
                </div>
                <p>Replace</p>
                <Input 
                    className='m-auto'
                    ref={getRef('replace')} 
                    placeholder='Replace' 
                    value={replace} 
                    setState={setReplace} 
                    next={getRef('example')}
                />
                <p>Example</p>
                <Input 
                    className='m-auto'
                    ref={getRef('example')} 
                    placeholder='Example' 
                    value={example} 
                    setState={setExample} 
                    onEnter={create} 
                />
            </div>
        </div>
    );
});

export default CreateBBCode;