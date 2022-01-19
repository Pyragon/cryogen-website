import React from 'react';

import Breadcrumb from './Breadcrumb';

export default function Breadcrumbs({ breadcrumbs }) {

    return (
        <div className="breadcrumbs">
            { breadcrumbs.map((breadcrumb, index) => 
                <Breadcrumb 
                    {...breadcrumb }
                    separator={index < breadcrumbs.length - 1 ? ' > ' : null}
                />
            )}
        </div>);
}
