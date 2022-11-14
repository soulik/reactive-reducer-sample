package com.example.reactor001reducer.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class HelloService {
    public Mono<ServerResponse> forLoop(ServerRequest request){
        Long N = Long.valueOf(request.pathVariable("count"));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(classicalReducer(N), Long.class);
    }

    public Mono<ServerResponse> reducer(ServerRequest request){
        Long N = Long.valueOf(request.pathVariable("count"));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(parallelReducer(N), Long.class);
    }

    private Flux<Long> generateSourceData(Long N){
        return Flux.generate( () -> 0, (state, sink) -> {
            Long value = state.longValue();
            sink.next(value);
            if (value >= N){
                sink.complete();
            }
            return state + 1;
        });
    }

    private Mono<Long> classicalReducer(Long N){
        return Mono.create(sink -> {

            AtomicReference<Long> sum = new AtomicReference<>(0L);
            try {
                generateSourceData(N).collectList().toFuture().get().forEach(
                    element -> {
                        sum.set(sum.get() + element);
                    }
                );
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            sink.success(sum.get());
        });
    }

    private Mono<Long> parallelReducer(Long N){
        return generateSourceData(N).reduce(Long::sum);
    }
}
