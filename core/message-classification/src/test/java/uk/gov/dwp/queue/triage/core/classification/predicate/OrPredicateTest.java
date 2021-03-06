package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class OrPredicateTest {

    private final FailedMessagePredicate alwaysTruePredicate = new BooleanPredicate(true);
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessagePredicate failedMessagePredicate1 = mock(FailedMessagePredicate.class);
    private final FailedMessagePredicate failedMessagePredicate2 = mock(FailedMessagePredicate.class);

    private final OrPredicate underTest = new OrPredicate(Arrays.asList(failedMessagePredicate1, failedMessagePredicate2));

    @Test
    public void shortCutEvaluationWhenFirstPredicateIsTrue() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(true);

        assertThat(underTest.test(failedMessage), is(true));

        verify(failedMessagePredicate1).test(failedMessage);
        verifyZeroInteractions(failedMessagePredicate2);
    }

    @Test
    public void bothPredicatesAreEvaluatedWhenFirstPredicateReturnsFalse() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(false);
        when(failedMessagePredicate2.test(failedMessage)).thenReturn(true);

        assertThat(underTest.test(failedMessage), is(true));

        verify(failedMessagePredicate1).test(failedMessage);
        verify(failedMessagePredicate2).test(failedMessage);
    }

    @Test
    public void allPredicatesReturnFalse() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(false);
        when(failedMessagePredicate2.test(failedMessage)).thenReturn(false);

        assertThat(underTest.test(failedMessage), is(false));

        verify(failedMessagePredicate1).test(failedMessage);
        verify(failedMessagePredicate2).test(failedMessage);
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = JacksonConfiguration.defaultObjectMapper();
        objectMapper.registerSubtypes(BooleanPredicate.class);

        final OrPredicate underTest = objectMapper.readValue(
                objectMapper.writeValueAsString(new OrPredicate(singletonList(alwaysTruePredicate))),
                OrPredicate.class
        );

        assertThat(underTest.getPredicates(), contains(alwaysTruePredicate));
    }

    @Test
    public void testDescribe() {
        when(failedMessagePredicate1.describe(any(StringDescription.class))).thenAnswer(withDescription("predicate1"));
        when(failedMessagePredicate2.describe(any(StringDescription.class))).thenAnswer(withDescription("predicate2"));

        assertThat(underTest.describe(new StringDescription()).getOutput(), is("( predicate1 OR predicate2 )"));

    }

    @Test
    public void toStringTest() {
        when(failedMessagePredicate1.describe(any(StringDescription.class))).thenAnswer(withDescription("predicate1"));
        when(failedMessagePredicate2.describe(any(StringDescription.class))).thenAnswer(withDescription("predicate2"));

        assertThat(underTest.toString(), is("( predicate1 OR predicate2 )"));
    }

    private Answer<Description> withDescription(String predicateDescription) {
        return invocation -> ((Description)invocation.getArgument(0)).append(predicateDescription);
    }
}