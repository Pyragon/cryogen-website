import React from 'react';

import TableSection from '../../utils/sections/TableSection';

const info = [
    'Click the search icon to begin searching through your sent messages. Search using filters with filter:value and separate with commas. True/false or yes/no can be used.',
    'Filters: to, subject, body, sent (date), between(date-date)',
    'Examples: to:cody, subject: example, between: (01/01/2022-01/31/2022)'
];

export default function SentSection() {
    let actions = [];
    return (
        <TableSection
            info={info}
            actions={actions}
        />
    )
}
