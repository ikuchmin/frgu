package ru.osslabs.frgu.services;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.osslabs.frgu.dao.PersistenceException;
import ru.osslabs.frgu.domain.FrguObject;
import ru.osslabs.frgu.mongodb.MongoDBClientFactory;
import ru.osslabs.frgu.mongodb.MongoDBFrguFacadeObjectDao;
import ru.osslabs.frgu.mongodb.MongoDBFrguObjectDao;

import javax.xml.soap.SOAPException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ilyalyakin on 17.07.15.
 */
@RestController
@RequestMapping("/frgu")
public class RESTController {

    @Autowired
    public FrguService frguService;

    @Autowired
    public MongoDBClientFactory mongoFactory;

    /**
     *
     * @param uId
     * @return
     */
    @RequestMapping(value = "{id}/current", method = RequestMethod.GET)
    @ResponseBody
    public FrguObject getCurrent(@PathVariable("id") String uId) {
            return new MongoDBFrguFacadeObjectDao(mongoFactory).current(uId);
    }

    /**
     * /.../{frguId}/update?type=PsPassport
     * @param frguId
     * @param objectType
     * @return
     */
    @JsonSerialize(using = FrguObjectJsonSerializer.class)
    @RequestMapping(value = "{frguId}/update", method = RequestMethod.GET)
    @ResponseBody
    public FrguObject updateObject(
            @PathVariable("frguId") String frguId,
            @RequestParam(value = "type", defaultValue = "") String objectType) {
        FrguObject object = null;
        try {
            object = frguService.getObjectData(Long.parseLong(frguId), objectType);
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        try {
            new MongoDBFrguObjectDao(mongoFactory).create(object);
            new MongoDBFrguFacadeObjectDao(mongoFactory).rebuild(frguId, object);
        } catch (PersistenceException e) {
            e.printStackTrace();
            //TODO throw 503
        }
        return object;
    }

    /**
     *
     * @param fromSSN
     * @return
     */
    @RequestMapping(value = "updateAll", method = RequestMethod.GET)
    @ResponseBody
    public boolean updateAll(@RequestParam(value = "fromSSN", defaultValue = "0") String fromSSN) {
        List<FrguObject> objects = frguService.updateDataBase(Long.parseLong(fromSSN));
        List<String> ids = objects.stream().map(FrguObject::getFrguId).collect(Collectors.toList());
        try {
            new MongoDBFrguObjectDao(mongoFactory).create(objects);
            new MongoDBFrguFacadeObjectDao(mongoFactory).rebuildAll(ids, objects);
        } catch (PersistenceException e) {
            e.printStackTrace();
            //TODO throw 503
        }
        return true;
    }

    /**
     * /.../objects?from={timestamp}
     *
     * @param timestamp
     * @return
     */
    @RequestMapping(value = "objects", method = RequestMethod.GET)
    @ResponseBody
    public List<FrguObject> getAll(
            @RequestParam(value = "from", defaultValue = "0") Long timestamp) {
        return new MongoDBFrguObjectDao(mongoFactory).changes(timestamp);
    }

    @RequestMapping(value = "services", method=RequestMethod.GET)
    public @ResponseBody String getTestMessage() {
        return "1./frgu/{id}/current\n" +
                "{id} - идентификатор объекта(из фргу)\n" +
                "Возвращает объект из базы данных. Ответ - объект FRGUObject\n" +
                "\n" +
                "2./frgu/{id}/update?type={type}\n" +
                "{id} - идентификатор объекта(из фргу)\n" +
                "{type} - тип объекта(PsPassport  или RStateStructure, регистрозависимо)\n" +
                "Обновляет объект в базе данных по данным фргу. Ответ - объект FRGUObject\n" +
                "\n" +
                "3./frgu/updateAll?fromSSN={fromSSN}\n" +
                "{fromSSN} - c какого SSN обновлять систему(необязательно, значение по умолчанию - 0)\n" +
                "Обновляет состояние всей системы. Ответ - bool(true - без ошибок)\n" +
                "\n" +
                "4./frgu/objects?from={from}\n" +
                "{from} - время, с которого нужно искать в формате long(необязательно, значение по умолчанию - 0)\n" +
                "Выводит список объектов, изменённых с определённого момента. Ответ - массив объектов FRGUObject\n" +
                "\n" +
                "5./frgu/services\n" +
                "Без параметров\n" +
                "Возвращает строку всех сервисов REST с описаниями";
    }
}
