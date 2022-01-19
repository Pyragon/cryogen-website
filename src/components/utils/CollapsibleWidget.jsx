import React from 'react'

import { Link } from 'react-router-dom';

import SlideToggle from 'react-slide-toggle';

const CollapsibleWidget = React.forwardRef(({ title, minimizable=true, link, onClick, description, index, children, collapsed=false, className="", style, contentStyle={} }, ref) => (
    <SlideToggle
        duration={1000}
        collapsed={collapsed}
        whenReversedUseBackwardEase={false}
        render={({ toggle, setCollapsibleElement, toggleState }) => (
            <div ref={ref} key={index} className={"news-post "+className} style={style}>
                <div className="header">
                    <div>
                        { link ? 
                            <Link to={link} className="news-post-title">
                                {title}
                            </Link>
                        : <span className="news-post-title" onClick={() => onClick && onClick()}>{title}</span> }
                        { minimizable && 
                            <div className="minimize" onClick={toggle}>
                                <i className={"fa "+(toggleState === 'COLLAPSED' ? "fa-plus" : "fa-minus")}/>
                            </div> 
                        }
                    </div>
                    { description && 
                        <p className="news-post-description">
                            {description}
                        </p> 
                    }
                </div>
                <div className="content">
                    <div className={"wrapper"} ref={setCollapsibleElement}>
                        <div className="news-post-text" style={{contentStyle}}>
                            {children}
                        </div>
                    </div>
                </div>
            </div>
        )}
    />
));

export default CollapsibleWidget;
