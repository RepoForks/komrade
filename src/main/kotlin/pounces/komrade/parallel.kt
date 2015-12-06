package pounces.komrade

import rx.Observable
import rx.Scheduler

// ReactiveX is concurrent, but by default, not parallel. Because message throttling blocks the thread using
// Thread.sleep(), and because other things may force the thread to block in a way RxJava can't handle, this method
// provides parallelism on any given scheduler. There are no guarantees of things coming out properly from the returned
// Observable because I don't know what I'm doing.
// See: https://github.com/ReactiveX/RxJava/issues/1673
fun <T, U> Observable<T>.parallel(scheduler: Scheduler, func: (T) -> U): Observable<U> =
        this.flatMap { Observable.just(it).subscribeOn(scheduler).map(func) }

