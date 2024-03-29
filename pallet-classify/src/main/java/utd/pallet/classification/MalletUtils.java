package utd.pallet.classification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import utd.pallet.classification.MalletTextDataTrainer.TrainerObject;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

// Not part of RDFUtils (of pallet-data) since TrainerObject is native to pallet-classify.
/**
 * Utility to convert RDF data to Trainer and Trainer to RDF data.
 * 
 */
public class MalletUtils {

    /**
     * @param rdf
     *            RDF data as string.
     * @param modelLang
     *            model language to be used in conversion.
     * @return Trainer object that is extracted from RDF data.
     * @throws Exception
     */
    public static TrainerObject convertRDFToTrainerObj(String rdf,
            String modelLang) throws Exception {

        Model model = ModelFactory.createDefaultModel();
        ByteArrayInputStream bisModel = new ByteArrayInputStream(rdf.getBytes());
        model.read(bisModel, modelLang);

        StmtIterator stmtItr = model.listStatements((Resource) null,
                OWL.hasValue, (RDFNode) null);
        Statement onlyStmt = stmtItr.nextStatement();

        ByteArrayInputStream bisLiteral = new ByteArrayInputStream(
                (byte[]) onlyStmt.getLiteral().getValue());
        ObjectInputStream ois = new ObjectInputStream(bisLiteral);
        TrainerObject trainerObject = (TrainerObject) ois.readObject();

        return trainerObject;

    }

    /**
     * @param model
     *            jena model to be used to create the Statement.
     * @param trnObj
     *            Object that needs to be converted to RDF statement.
     * @param trnObjUri
     *            Resource URI to be used.
     * @return RDF statement created.
     * @throws Exception
     */
    public static Statement convertTrainertoRDFStatement(Model model,
            TrainerObject trnObj, String trnObjUri) throws Exception {

        Resource res = model.createResource(trnObjUri);
        Property prop = OWL.hasValue;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(trnObj);

        Literal obj = model.createTypedLiteral(bos.toByteArray());

        Statement stmt = model.createLiteralStatement(res, prop, obj);

        return stmt;
    }
}
