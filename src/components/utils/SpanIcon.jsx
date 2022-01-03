import React from 'react'

export default function SpanIcon({ className, onClick, icon, content}) {
    return (
        <div className={className}>
           <i onClick={onClick}className={"fa "+icon+" c-pointer"}></i>
            <span>
                { " "+content }
            </span> 
        </div>
    )
}
