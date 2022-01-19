import React from 'react';

import '../../../styles/utils/RichTextEditor.css';

export default function RichTextEditor({ value, style={}, setState}) {
    let defaultStyle = {
        height: '8rem',
        width: '100%',
        minHeight: '8rem',
    };
    let extended = { ...defaultStyle, ...style };
    return (
        <>
            <div style={{...style, height: '2rem'}}className="text-editor-options">
            </div>
            <textarea 
                className="text-editor" 
                style={extended}
                value={value} 
                onChange={(e) => setState(e.target.value)}
            />
        </>
    )
}
