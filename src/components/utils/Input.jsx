import React from 'react'

export default function Input({ className="", style, value="", type="text", placeholder="", setState, onEnter }) {
    return (
        <input className={"input "+className} 
            style={style} 
            value={value} 
            type={type} 
            placeholder={placeholder} 
            onChange={e => setState(e.target.value)}
            onKeyDown={e => {
                if(onEnter && e.key === 'Enter')
                    onEnter();
            }}/>
    )
}
