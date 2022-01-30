import React, { useContext } from 'react';

import Widget from '../Widget';
import Button from '../Button';
import SectionContext from '../../../utils/contexts/SectionContext';

import '../../../styles/utils/Section.css';

export default function Sections({ sections, active }) {
    let { setSection } = useContext(SectionContext);
    return (
        <div key={active.title} className='section-container'>
            <div className='section-list'>
                { sections.map((section, index) => 
                    <Button
                        key={index}
                        title={section.title}
                        className={'section-btn '+(section.title === active.title ? 'active' : '')}
                        onClick={() => setSection(section.title)}
                    />
                )}
            </div>
            <div className='section-content'>
                <Widget 
                    title={active.title}
                    description={active.description}
                >
                    { active.content }
                </Widget>
            </div>
        </div>
    )
}
