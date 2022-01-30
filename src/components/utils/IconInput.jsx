import React from 'react';

export default function IconInput({ icon, type='input', className, placeholder, value, setState}) {
    return (
        <div className={className}>
            <i className={'fa '+icon} />
            <input 
                className='input'
                style={{width: 'auto'}}
                type={type} 
                placeholder={placeholder} 
                value={value} 
                onChange={e => setState(e.target.value)}
            />
        </div>
    );
}
