import React from 'react'

import FooterButton from './FooterButton';

const buttons = require('./buttons.json');

export default function Footer() {
    let className = "footer";
    let loggedIn = true;
    if(loggedIn) //logged-in
        className += " footer-4";
    return (
        <div className={className}>
            { buttons.map((button, index) => {
                if(button.requiresStaff && !loggedIn)
                    return (<></>);
                return(
                <div key={index}>
                    <h3>{button.header}</h3>
                    <ul>
                        {button.buttons.map((button, index) => (
                            <FooterButton key={index} button={button} index={index}/>
                        ))}
                    </ul>
                </div>
            ) })}
        </div>
    )
}
