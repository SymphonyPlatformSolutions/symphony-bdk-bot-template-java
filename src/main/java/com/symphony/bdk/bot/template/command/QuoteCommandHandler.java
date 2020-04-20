package com.symphony.bdk.bot.template.command;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;

import com.symphony.bdk.bot.template.command.model.InternalQuote;
import com.symphony.bdk.bot.template.command.model.QuoteResponse;
import com.symphony.bdk.bot.sdk.command.CommandHandler;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.lib.restclient.model.RestResponse;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

/**
 * Sample code. CommandHandler that uses {@link RestClient} to consume external API to get currency
 * quotes.
 */
public class QuoteCommandHandler extends CommandHandler {
  	
  @Value("${samples.quote-command.quote-url}")
  private String QUOTE_URL;
  private static final String QUOTE_COMMAND = "/quote";

  private RestClient restClient;

  public QuoteCommandHandler(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " " + QUOTE_COMMAND)
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    Optional<String> currency = getCommandCurrency(command.getMessageEvent().getMessage());
    if (currency.isPresent()) {
      RestResponse<QuoteResponse> response = requestQuote(currency.get());
      if (response.getStatus() == 200) {
        QuoteResponse quoteResponse = response.getBody();
        InternalQuote iQuote = new InternalQuote(quoteResponse.getQuote());
        commandResponse.setEnrichedTemplateFile("quote-result", iQuote,
            "com.symphony.ms.currencyQuote", iQuote, "1.0");
      }
    } else {
      commandResponse.setMessage("Please provide the currency you want quote for");
    }
  }

  private Optional<String> getCommandCurrency(String commandMessage) {
    String[] commandSplit = commandMessage.split(" " + QUOTE_COMMAND + " ");
    if (commandSplit.length > 1) {
      String currency = commandSplit[1];
      if (currency != null) {
        return Optional.of(currency);
      }
    }
    return Optional.empty();
  }

  private RestResponse<QuoteResponse> requestQuote(String currency) {
    return restClient.getRequest(
        String.format(QUOTE_URL, currency), QuoteResponse.class);
  }

}
