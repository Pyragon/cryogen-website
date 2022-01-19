export default function generateBreadcrumbs({ thread, subforum, user, extend }) {
    if (!thread && !subforum && !user) return [{ title: 'Home', id: -1 }];
    if (user)
        return [{ title: 'Home', link: '/forums', id: -1 }, { title: user.displayName, id: user._id }];
    let breadcrumbs = [];
    if (extend)
        breadcrumbs.push(extend);
    if (thread) {
        let breadcrumb = { title: thread.title, id: thread._id };
        if (extend)
            breadcrumb.link = `/forums/${thread.subforum}/${thread._id}`;
        breadcrumbs.push(breadcrumb);
        breadcrumbs.push({
            title: thread.subforum.name,
            link: `/forums/${thread.subforum._id}`,
            id: thread.subforum._id
        });
    }
    if (subforum) {
        let breadcrumb = { title: subforum.name, id: subforum._id };
        if (extend)
            breadcrumb.link = `/forums/${subforum._id}`;
        breadcrumbs.push(breadcrumb);
    }

    subforum = subforum || thread.subforum;
    while ((subforum = subforum.parent) != null)
        breadcrumbs.push({
            title: subforum.name,
            link: `/forums/${subforum._id}`,
            id: subforum._id
        });

    breadcrumbs.push({
        title: 'Home',
        link: '/forums',
        id: -1
    });

    return breadcrumbs.reverse();
}