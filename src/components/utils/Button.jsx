import React from 'react';

export default React.forwardRef(({ title, style, onClick, className="", defaultClassName="btn ", children}, ref) => {
    children = children || title;
    style = style || {};
    return (
        <button 
            className={defaultClassName+className} 
            onClick={onClick}
            style={style}
            ref={ref}
        >
            {children}
        </button>
    )
});
