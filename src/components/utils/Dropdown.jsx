import React, { useLayoutEffect, useState, useRef } from 'react';

import Button from './Button';

import '../../styles/utils/Dropdown.css';
import { useEffect } from 'react';

export default function Dropdown({ title, className, options}) {
    let [ open, setOpen ] = useState(false);
    let buttonRef = useRef();
    let dropdownRef = useRef();

    let moveDropdown = function() {
        let dropdown = buttonRef.current;
        let dropdownContent = dropdownRef.current;
        if(!dropdownContent) return;

        dropdownContent.style.top = (dropdown.offsetTop + dropdown.offsetHeight - 1) + 'px';
        dropdownContent.style.left = dropdown.offsetLeft + 'px';
        dropdownContent.style.width = (dropdown.offsetWidth - 2) + 'px';
    }

    useLayoutEffect(moveDropdown);

    useEffect(() => window.addEventListener('resize', moveDropdown), []);

    let style = open ? { 
        borderBottomLeftRadius: '0',
        borderBottomRightRadius: '0',
    } : {
        borderBottomLeftRadius: '5px',
        borderBottomRightRadius: '5px',
    }

    return (
        <div className={className}>
            <Button 
                className="dropdown small" 
                style={style}
                ref={buttonRef}
                onClick={() => setOpen(!open)}
            >
                <span>{title}</span>
                <i className="fas fa-chevron-down"/>
            </Button>
            { open && 
                <div 
                    className="dropdown-content small"
                    ref={dropdownRef}
                >
                    { options.map(option => (
                        <div 
                            key={option.title}
                            onClick={option.onClick}
                        >
                            <i className={option.icon}/>
                            <span>{option.title}</span>
                        </div>
                    )) }
                </div>
            }
        </div>
    );
}
