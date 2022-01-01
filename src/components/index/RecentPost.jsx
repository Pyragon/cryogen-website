import React from 'react'

import SlideToggle from 'react-slide-toggle';

export default function RecentPost({post, index}) {
    return (
        <SlideToggle
            duration={1000}
            collapsed={index !== 0 && index !== 1}
            whenReversedUseBackwardEase={false}
            render={({ toggle, setCollapsibleElement, toggleState }) => (
                <div key={index} className="news-post ">
                    <div className="header">
                        <p className="title">
                            {post.title}
                            <a className="minimize" onClick={toggle}>
                                <span className={"fa "+(toggleState == 'COLLAPSED' ? "fa-plus" : "fa-minus")}/>
                            </a>
                        </p>
                        <p className="author">
                            Posted Today by {post.author}
                        </p>
                    </div>
                    <div className="content">
                        <div className={"wrapper"} ref={setCollapsibleElement}>
                            <p className="news-post-text">
                                {post.content}
                            </p>
                        </div>
                    </div>
                </div>
            )}
        />
    )
}
