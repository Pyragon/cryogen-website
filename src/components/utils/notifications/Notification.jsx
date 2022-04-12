import React, { createRef } from 'react';

export default function Notification({ id, text, onClick, children, popout }) {
    let notification = createRef();
    setTimeout(() => {
        if(!notification.current) return;
        notification.current.className = 'notification';
        console.log(notification.current);
    }, 500);
    return (
        <div className={'notification '+(popout ? 'popout' : '')} key={id} onClick={onClick} ref={notification}>
            { text && <div className='notification-value'>{text}</div> }
            { children}
        </div>
    );
}
