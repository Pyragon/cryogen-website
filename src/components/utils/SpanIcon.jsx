import React from 'react'

export default function SpanIcon({ className, onClick, icon, children, style}) {
    return (
        <div className={className} style={style}>
           <i onClick={onClick}className={"fa "+icon+" c-pointer"}></i>
            <span>
                {children}
            </span> 
        </div>
    )
}
