import React from 'react';

export default React.forwardRef(({ id, text, onClick, children }, ref) => {
    return (
        <div className='notification popout' key={id} onClick={onClick} ref={ref}>
            { text && <div className='notification-value'>{text}</div> }
            { children}
        </div>
    );
});
