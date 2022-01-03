import React from 'react'

import FooterButton from './FooterButton';

const buttons = require('./buttons.json');

export default function Footer() {
    let loggedIn = true;
    let isAdmin = true;
    return (
        <div className="footer">
            <div className={"footer-btns "+(loggedIn && isAdmin ? "footer-btns-4" : "")}>
                { buttons.map((button, index) => {
                    if(button.requiresStaff && (!loggedIn || !isAdmin))
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
        </div>
    )
}
