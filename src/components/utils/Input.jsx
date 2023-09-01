import React from 'react';

const Input = React.forwardRef(({ className="", style, value="", type="text", placeholder="", setState, onEnter, next }, ref) => {
    return (
        <input className={"input "+className} 
            style={style} 
            value={value} 
            type={type}
            ref={ref}
            placeholder={placeholder} 
            onChange={e => setState(e.target.value)}
            onKeyDown={e => {
                if(e.key === 'Enter') {
                    console.log('hit enter, value:', value);
                    if(next) next.current.focus();
                    else if(onEnter) onEnter(value);
                }
            }}/>
    )
});

export default Input;
