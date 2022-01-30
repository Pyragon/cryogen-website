import React from 'react';

import TableSection from '../../utils/sections/TableSection';

const info = [
    'Click the search icon to begin searching through your drafts. Search using filters with filter:value and separate with commas. True/false or yes/no can be used.',
    'Filters: to, subject, body',
    'Examples: to:cody, subject: example'
];

export default function DraftSection() {
    let actions = [];
    return (
        <TableSection
            info={info}
            actions={actions}
        />
    )
}
