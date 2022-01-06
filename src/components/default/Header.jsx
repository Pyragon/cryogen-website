import React from 'react';

import NavButton from './NavButton';

export default function Header() {
    return (
        <>
            <div className="navbar">
                <img src="/images/logo.png" className="logo-img" alt="logo" />
                <div className="nav-btns">
                    <NavButton link="/" title="Homepage" />
                    <NavButton onClick={() => console.log('dropdown')} title="Play" /> {/* TODO: create dropdown */}
                    <NavButton link="/forums" title="Community" />
                    <NavButton link="/highscores" title="Highscores" />
                    <NavButton link="/movie-night" title="Movie Night" />
                    <NavButton link="/user" title="Account" />
                </div> 
            </div>
        </>
    )
}
