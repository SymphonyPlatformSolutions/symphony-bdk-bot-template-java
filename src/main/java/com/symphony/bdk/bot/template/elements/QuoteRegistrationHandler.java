package com.symphony.bdk.bot.template.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.elements.ElementsHandler;
import com.symphony.bdk.bot.sdk.event.model.SymphonyElementsEvent;
import com.symphony.bdk.bot.sdk.symphony.UsersClient;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyUser;

/**
 * Sample code. Implementation of {@link ElementsHandler} which renders a Symphony elements form and
 * handles its submission.
 */
public class QuoteRegistrationHandler extends ElementsHandler {
  private static final String FORM_ID = "quo-register-form";
  private static final String FROM_CURRENCY = "fromCurrency";
  private static final String TO_CURRENCY = "toCurrency";
  private static final String AMOUNT = "amount";
  private static final String ASSIGNED_TO = "assignedTo";

  private final UsersClient usersClient;
  
  public QuoteRegistrationHandler(UsersClient usersClient) {
    this.usersClient = usersClient;
  }
  
  /**
   * Used by CommandFilter to filter Symphony chat messages
   */
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /register quote$")
        .asPredicate();
  }

  @Override
  protected String getElementsFormId() {
    return FORM_ID;
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void displayElements(BotCommand command, SymphonyMessage elementsResponse) {
    Map<String, String> data = new HashMap<>();
    data.put("form_id", getElementsFormId());
    elementsResponse.setTemplateFile("quote-registration", data);
  }

  /**
   * Invoked when elements form is submitted
   */
  @Override
  public void handleAction(SymphonyElementsEvent event, SymphonyMessage elementsResponse) {
    Map<String, Object> formValues = event.getFormValues();
    Map<String, Object> data = new HashMap<String, Object>();
    data.put(FROM_CURRENCY, formValues.get(FROM_CURRENCY).toString().toUpperCase());
    data.put(TO_CURRENCY, formValues.get(TO_CURRENCY).toString().toUpperCase());
    data.put(AMOUNT, formValues.get(AMOUNT));
    try {
      List<Long> personSelector = (List<Long>)formValues.get(ASSIGNED_TO);
      SymphonyUser dealer = usersClient.getUserFromId(personSelector.get(0), true);
      data.put(ASSIGNED_TO, dealer.getDisplayName());
      elementsResponse.setTemplateMessage(
          "Quote FX {{fromCurrency}}-{{toCurrency}} {{amount}} sent to dealer {{assignedTo}}", data);
    } catch (SymphonyClientException sce) {
      // Handle failure retrieving dealer details
    }
  }

}
