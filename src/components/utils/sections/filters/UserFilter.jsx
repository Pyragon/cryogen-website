import React from 'react';
import FilterContainer from './FilterContainer';

import DisplayUser from '../../user/DisplayUser';

export default function UserFilter({ filter }) {
    return (
        <FilterContainer>
            <DisplayUser
                user={filter.user}
                prefix={filter.title+': '}
            />
        </FilterContainer>
    );
}
