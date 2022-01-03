import React from 'react'

import SlideToggle from 'react-slide-toggle';

export default function NewsPost({ title, description, children, index }) {

    return (
        <SlideToggle
            duration={1000}
            collapsed={index !== 0 && index !== 1}
            whenReversedUseBackwardEase={false}
            render={({ toggle, setCollapsibleElement, toggleState }) => (
                <div key={index} className="news-post ">
                    <div className="header">
                        <p className="title">
                            {title}
                            <a className="minimize" onClick={toggle}>
                                <span className={"fa "+(toggleState == 'COLLAPSED' ? "fa-plus" : "fa-minus")}/>
                            </a>
                        </p>
                        <p className="author">
                            {description}
                        </p>
                    </div>
                    <div className="content">
                        <div className={"wrapper"} ref={setCollapsibleElement}>
                            <div className="news-post-text">
                                {children}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        />
    )
}
