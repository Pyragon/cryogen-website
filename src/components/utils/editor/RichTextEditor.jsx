import React from 'react';

import '../../../styles/utils/RichTextEditor.css';

export default function RichTextEditor({ height, width, setState}) {
    let style = {
        height: height || '8rem',
        width: width || '100%',
        minHeight: height || '8rem',
    }
    return (
        <>
            <div className="text-editor-options">
            </div>
            <textarea 
                className="text-editor" 
                style={style} 
                onChange={(e) => setState(e.target.value)}
            />
        </>
    )
}
