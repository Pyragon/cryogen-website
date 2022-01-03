import React from 'react';

export default function ForumStat({ title, value}) {
    return (
        <div className="forum-stats">
            <div className="forum-stats-item">
                <p className="forum-stats-item-title">{title+':'}</p>
                <p className="forum-stats-item-value">{value}</p>
            </div>
        </div>
    )
}
