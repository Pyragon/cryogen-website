import React, { useLayoutEffect, useState, useRef } from 'react';

import Button from './Button';

import '../../styles/utils/Dropdown.css';
import { useEffect } from 'react';

export default function Dropdown({ title, className='', options, useGrid }) {
    let [ open, setOpen ] = useState(false);
    let buttonRef = useRef();
    let dropdownRef = useRef();

    let resizeDropdown = function() {
        let dropdown = buttonRef.current;
        let dropdownContent = dropdownRef.current;
        if(!dropdownContent) return;
        dropdownContent.style.width = (dropdown.offsetWidth - 2) + 'px';
    }

    useLayoutEffect(resizeDropdown);

    useEffect(() => window.addEventListener('resize', resizeDropdown), []);

    let style = open ? { 
        borderBottomLeftRadius: '0',
        borderBottomRightRadius: '0',
    } : {
        borderBottomLeftRadius: '5px',
        borderBottomRightRadius: '5px',
    }

    let openClass = useGrid ? 'dropdown-open-grid' : 'dropdown-open';

    return (
        <div className={'dropdown '+className}>
            <Button 
                style={style}
                ref={buttonRef}
                onClick={() => { setOpen(!open); console.log(dropdownRef.current.getBoundingClientRect()) }}
            >
                <span>{title}</span>
                <i className={'fas fa-chevron-'+(open ? 'up' : 'down')}/>
            </Button>
            <div 
                className={'dropdown-content small ' +(open ? openClass : '')}
                ref={dropdownRef}
            >
                { options.map(option => {
                    return (
                        <div 
                            key={option.title}
                            onClick={() => {
                                setOpen(false);
                                option.onClick();
                            }}
                        >
                            <i className={option.icon}/>
                            <span>{option.title}</span>
                        </div>
                    )
                 }) }
            </div>
        </div>
    );
}
