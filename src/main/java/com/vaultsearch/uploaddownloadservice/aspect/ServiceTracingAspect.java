package com.vaultsearch.uploaddownloadservice.aspect;

import com.vaultsearch.uploaddownloadservice.model.FileMetadata;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ServiceTracingAspect {

    private final Tracer tracer;

    @Pointcut("execution(* com.vaultsearch.uploaddownloadservice.service.FileStorageService.uploadFile(..))")
    public void uploadFileService() {}

    @Around("uploadFileService() && args(file)")
    public Object traceUploadFile(ProceedingJoinPoint pjp, MultipartFile file) throws Throwable {
        var spanBuilder = tracer.spanBuilder("FileStorageService.uploadFile");
        Span span = spanBuilder.startSpan();
        try (var scope = span.makeCurrent()) {
            // Pre-call attributes from request
            if (file != null) {
                safeSet(span, AttributeKey.stringKey("file.name"), file.getOriginalFilename());
                safeSet(span, AttributeKey.stringKey("file.content_type"), file.getContentType());
                safeSet(span, AttributeKey.longKey("file.size"), file.getSize());
            }
            Object result = pjp.proceed();

            // Post-call attributes from the result
            if (result instanceof FileMetadata fm) {
                safeSet(span, AttributeKey.stringKey("content.id"), fm.getContentId());
                safeSet(span, AttributeKey.stringKey("aws.s3.key"), fm.getS3Key());
            }
            span.setStatus(StatusCode.OK);
            return result;
        } catch (Throwable ex) {
            span.recordException(ex);
            span.setStatus(StatusCode.ERROR, ex.getMessage());
            throw ex;
        } finally {
            span.end();
        }
    }

    private static void safeSet(Span span, AttributeKey<String> key, String value) {
        if (value != null) {
            span.setAttribute(key, value);
        }
    }

    private static void safeSet(Span span, AttributeKey<Long> key, long value) {
        span.setAttribute(key, value);
    }
}
