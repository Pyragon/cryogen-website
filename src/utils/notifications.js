import ReactDOMServer from 'react-dom/server';

function sendNotification({ text, theme = 'cryogen', layout = 'topRight', timeout = 5000, template, buttons }) {
    let Noty = window.noty;
    let options = {
        text,
        theme,
        layout,
        timeout,
    };
    if (buttons)
        options.buttons = buttons;
    if (template)
        options.template = ReactDOMServer.renderToStaticMarkup(template);
    Noty(options);
}

function sendErrorNotification(error) {
    console.error(error);
    sendNotification({
        template: <p style={{color: 'red', padding: '.5rem'}}>{error.message || error}</p>
    });
}

export { sendNotification, sendErrorNotification };