export default function generateBreadcrumbs({ thread, subforum, user }) {
    if (!thread && !subforum && !user) return [{ title: 'Home', id: -1 }];
    if (user)
        return [{ title: 'Home', link: '/forums', id: -1 }, { title: user.displayName, id: user._id }];
    let breadcrumbs = [];
    if (thread) {
        breadcrumbs.push({ title: thread.title, id: thread._id });
        breadcrumbs.push({
            title: thread.subforum.name,
            link: `/forums/${thread.subforum._id}`,
            id: thread.subforum._id
        });
    }
    if (subforum)
        breadcrumbs.push({ title: subforum.name, id: subforum._id });

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