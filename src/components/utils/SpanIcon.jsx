import React from 'react'

export default function SpanIcon({ className, onClick, icon, children}) {
    return (
        <div className={className}>
           <i onClick={onClick}className={"fa "+icon+" c-pointer"}></i>
            <span>
                {children}
            </span> 
        </div>
    )
}
