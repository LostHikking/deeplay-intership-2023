package io.deeplay.grandmastery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.deeplay.grandmastery.dto.IDto;
import io.deeplay.grandmastery.exceptions.QueryException;

public class ConversationService {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Метод сериализует IDto объект.
   *
   * @param object Объект
   * @return Json строка
   * @throws QueryException Ошибка выполнения запроса
   */
  public static String serialize(IDto object) throws QueryException {
    try {
      return MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new QueryException("Ошибка при сериализаци объекта - " + object);
    }
  }

  /**
   * Метод десериализует строку в IDto объект.
   *
   * @param json Строка
   * @return Объект
   * @throws QueryException Ошибка выполнения запроса
   */
  public static IDto deserialize(String json) throws QueryException {
    try {
      return MAPPER.readValue(json, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new QueryException("Ошибка при десериализаци строки - " + json);
    }
  }

  /**
   * Метод десериализует строку в tClass объект.
   *
   * @param json Строка
   * @param clazz Класс
   * @return Объект
   * @throws QueryException Ошибка выполнения запроса
   */
  public static <T extends IDto> T deserialize(String json, Class<T> clazz) throws QueryException {
    try {
      return MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new QueryException("Ошибка при десериализаци строки - " + json + " в класс " + clazz);
    }
  }
}
