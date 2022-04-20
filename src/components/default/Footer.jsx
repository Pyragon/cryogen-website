import React, { useContext } from 'react'
import UserContext from '../../utils/contexts/UserContext';

import FooterButton from './FooterButton';

const buttons = require('./buttons.json');

export default function Footer() {
    let { user } = useContext(UserContext);
    let isAdmin = user && user.displayGroup.rights > 0;
    return (
        <div className="footer">
            <div className={"footer-btns "+(user && isAdmin ? "footer-btns-4" : "")}>
                { buttons.filter(button => !button.requiresStaff || isAdmin)
                    .map((button, index) => 
                        <div key={index}>
                            <h3>{button.header}</h3>
                            <ul>
                                {button.buttons.map((button, index) => (
                                    <FooterButton key={index} button={button} index={index}/>
                                ))}
                            </ul>
                        </div>
                    )
                }
            </div>
        </div>
    )
}
