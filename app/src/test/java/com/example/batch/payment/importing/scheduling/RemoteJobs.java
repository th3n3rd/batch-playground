package com.example.batch.payment.importing.scheduling;

import com.example.batch.payment.importing.worker.ImportJob;
import java.util.Optional;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

public class RemoteJobs {
    private final MessageChannel requests;
    private final PollableChannel replies;

    RemoteJobs(
        @Qualifier("requestsChannel") MessageChannel requests,
        @Qualifier("repliesChannel") PollableChannel replies
    ) {
        this.requests = requests;
        this.replies = replies;
    }

    public void schedule(ImportJob importJob) {
        requests.send(new GenericMessage<>(importJob));
    }

    public boolean lastJobFinished() {
        return nextReply().map(it -> !it.isRunning()).orElse(false);
    }

    private Optional<JobExecution> nextReply() {
        var message = replies.receive();
        return message == null
            ? Optional.empty()
            : Optional.of((JobExecution) message.getPayload());
    }
}
