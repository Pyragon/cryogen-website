import React, { createRef } from 'react';

import '../../../styles/utils/RichTextEditor.css';
import RichTextEditorButton from './RichTextEditorButton';

const buttons = require('./buttons.json');

export default function RichTextEditor({ value, style, setState }) {
    let defaultStyle = {
        height: '8rem',
        width: '100%',
        minHeight: '8rem',
    };
    let ref = createRef();
    let extended = { ...defaultStyle, ...style };
    return (
        <>
            <div style={{...style, height: '2rem'}} className="text-editor-options">
                { buttons.map((button, index) => <RichTextEditorButton value={value} setState={setState} button={button} key={index} ref={ref}/>) }
            </div>
            <textarea 
                className="text-editor" 
                style={extended}
                value={value} 
                onChange={(e) => setState(e.target.value)}
                ref={ref}
            />
        </>
    )
};
