import React from 'react';

import '../../styles/utils/DiscordWidget.css';

export default function DiscordWidget() {
    return (
        <iframe 
            title='discord-widget'
            className='discord-widget'
            src="https://discord.com/widget?id=199274190468022273&theme=dark" 
            height="350" 
            allowtransparency="true" 
            frameBorder="0" 
            sandbox="allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts"
        />
    )
}
