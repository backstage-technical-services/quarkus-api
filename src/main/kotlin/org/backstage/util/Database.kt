package org.backstage.util

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase

fun <E, I> PanacheRepositoryBase<E, I>.update(entity: E) = persist(entity)
