import React from 'react';

const RichTextEditorButton = React.forwardRef(({ button, value, setState }, ref) => {
    let click = button => {
    
        let textarea = ref.current;
        let highlighted = '';
        let cursorPos = textarea.selectionEnd;
        if(textarea.selectionStart !== textarea.selectionEnd && button.text.includes('$highlight')) {
            highlighted = textarea.value.substring(textarea.selectionStart, textarea.selectionEnd);
            cursorPos = textarea.selectionStart + button.text.indexOf('$highlight') + highlighted.length + (button.text.length - '$highlight'.length - button.text.indexOf('$highlight'));
        } else
            cursorPos = textarea.selectionStart + (button.text.includes('$highlight') ? button.text.indexOf('$highlight') : button.text.length);

        let newText = value.substring(0, textarea.selectionStart) + button.text.replace('$highlight', highlighted) + value.substring(textarea.selectionEnd);

        setState(newText);

        textarea.focus();
        setTimeout(() => {
            textarea.selectionStart = cursorPos;
            textarea.selectionEnd = cursorPos;
        }, 0);


    };
    return (
        <i className=
            {button.icon} 
            title={button.name}
            onClick={() => click(button)}
        />
    )
});

export default RichTextEditorButton;
