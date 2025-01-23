package org.kwi.data

/**
 * A data source that is also loadable.
 */
interface ILoadableDataSource<T> : IDataSource<T>, ILoadable

