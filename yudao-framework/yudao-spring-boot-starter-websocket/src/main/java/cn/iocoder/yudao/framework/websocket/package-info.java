/**
 * WebSocket 框架，支持多节点的广播
 *
 * 这个架构的底层通过配置项来指定启动时，服务端广播WebSocket时的方式。
 * 只是本机（JVM）、redis、rabbitMq、RocketMQ、Kafka等作为消息中间件透传WebSocket消息然后再广播给webSocket客户端。
 * 之所以在服务中引入MQ作为中间件，其目的是为了削峰，即防止服务端应用广播的webSocket在内存中处理缓慢影响广播速度，因此采用MQ作为中间件缓冲消息。
 * 在服务中引入MQ，缓冲了webSocket消息后，再进行自消费，拿到MQ中缓冲的webSocket消息，通过消息中携带的sessionId获取webSocketSession对象。
 * 进一步将webSocket消息推送到指定的webSocket客户端。
 * 等于是中间加入了MQ作为了一个webSocket的解耦中间件，达到消息异步、解耦、削峰的原理，减轻应用服务器本身处理webSocket消息的压力。
 */
package cn.iocoder.yudao.framework.websocket;
