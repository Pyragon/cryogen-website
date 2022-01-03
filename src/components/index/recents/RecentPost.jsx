import React, { useState } from 'react'
import SlideToggle from 'react-slide-toggle';

import { crownUser } from '../../../utils/format';

export default function RecentPost({thread, index}) {
    let post = thread.posts[0];
    thread = thread.thread;
    return (
        <SlideToggle
            duration={1000}
            collapsed={index !== 0 && index !== 1}
            whenReversedUseBackwardEase={false}
            render={({ toggle, setCollapsibleElement, toggleState }) => (
                <div key={index} className="news-post ">
                    <div className="header">
                        <p className="title">
                            {thread.title}
                            <a className="minimize" onClick={toggle}>
                                <span className={"fa "+(toggleState == 'COLLAPSED' ? "fa-plus" : "fa-minus")}/>
                            </a>
                        </p>
                        <p className="author">
                            Posted Today by {crownUser(thread.author)}
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
