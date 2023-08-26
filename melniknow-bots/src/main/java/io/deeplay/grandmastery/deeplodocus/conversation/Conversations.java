package io.deeplay.grandmastery.deeplodocus.conversation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.deeplay.grandmastery.deeplodocus.exceptions.DeeplodocusClientException;

public class Conversations {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Метод сериализует IDto объект.
   *
   * @param object Объект
   * @return Json строка
   * @throws DeeplodocusClientException Ошибка выполнения запроса
   */
  public static String getJsonFromObject(Object object) throws DeeplodocusClientException {
    try {
      return MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new DeeplodocusClientException(
          "Ошибка при сериализаци объекта - " + object, e.getCause());
    }
  }

  /**
   * Метод десериализует строку в clazz объект.
   *
   * @param json Строка
   * @param clazz Класс
   * @return Объект
   * @throws DeeplodocusClientException Ошибка выполнения запроса
   */
  public static <T> T getObjectFromJson(String json, Class<T> clazz)
      throws DeeplodocusClientException {
    try {
      return MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new DeeplodocusClientException(
          "Ошибка при десериализаци строки - " + json, e.getCause());
    }
  }
}
