package io.github.akoserwal;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import com.avro.events.streaming.UserRegisteredEvent;

public class KeycloakCustomEventListener implements EventListenerProvider {

  private final KeycloakSession session;
  private final RealmProvider model;

  public KeycloakCustomEventListener(KeycloakSession session) {
    this.session = session;
    this.model = session.realms();
  }

  @Override
  public void onEvent(Event event) {
    if (EventType.REGISTER.equals(event.getType())) {
      RealmModel realm = this.model.getRealm(event.getRealmId());
      UserModel registeredUser = this.session.users().getUserById(realm, event.getUserId());

      UserRegisteredEvent registeredEvent = new UserRegisteredEvent();
      registeredEvent.setId(registeredUser.getId());
      registeredEvent.setFirstName(registeredUser.getFirstName());
      registeredEvent.setLastName(registeredUser.getLastName());
      registeredEvent.setEmail(registeredUser.getEmail());


      Producer.publishEvent("user-registered", registeredEvent);
    }
  }

  @Override
  public void onEvent(AdminEvent adminEvent, boolean b) {

  }

  @Override
  public void close() {

  }
}
