package utd.pallet.classification;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;

/**
 * Creates classifier by training it on the data specified.
 * 
 * 
 *
 */
public class MalletTextDataTrainer {
	
	public static final int ALGO_UNASSIGNED = 0x00;
	public static final int NAIVEBAYES 		= 0x01;
	public static final int MAXENT	 		= 0x02;
	public static final int DECISIONTREES 	= 0x03;
	public static final int C45				= 0x04;
	public static final int BALANCEDWINNOW	= 0x06;
	public static final int RANKMAXENT		= 0x07;
	public static final int NAIVEBAYES_EM	= 0x08;
	public static final int MAXENT_GE		= 0x09;
	public static final int MC_MAXENT		= 0x0A;
	
	/**
	 * Creates Trainer instance 
	 */
	public MalletTextDataTrainer () { }
		
	@SuppressWarnings("unchecked")

	/*
	 * Fetches the training algorithm to be used for incremental training based on the instance of previous trainer.
	 * 
	 * @param trainer that used to train data earlier.
	 * @return Training algorithm used.
	 */
	private int getTrainerAlgo (ClassifierTrainer trainer) throws java.lang.NullPointerException {
		
		if (trainer == null)
			throw new NullPointerException ("Trainer not initialized");
					
		int trainerAlgo = ALGO_UNASSIGNED;
		
		if (trainer instanceof cc.mallet.classify.NaiveBayesTrainer)
			trainerAlgo = NAIVEBAYES;
		else if (trainer instanceof cc.mallet.classify.MaxEntTrainer)
			trainerAlgo = MAXENT;
		else if (trainer instanceof cc.mallet.classify.DecisionTreeTrainer)
			trainerAlgo = DECISIONTREES;
		else if (trainer instanceof cc.mallet.classify.C45Trainer)
			trainerAlgo = C45;
		else if (trainer instanceof cc.mallet.classify.BalancedWinnowTrainer)
			trainerAlgo = BALANCEDWINNOW;		
		else if (trainer instanceof cc.mallet.classify.RankMaxEntTrainer)
			trainerAlgo = RANKMAXENT;
		else if (trainer instanceof cc.mallet.classify.NaiveBayesEMTrainer)
			trainerAlgo = NAIVEBAYES_EM;
		else if (trainer instanceof cc.mallet.classify.MaxEntGETrainer)
			trainerAlgo = MAXENT_GE;
		else if (trainer instanceof cc.mallet.classify.MCMaxEntTrainer)
				trainerAlgo = MC_MAXENT;
							
		return trainerAlgo;
		
	}

	/*
	 * Creates trainer from the factory based on the specified Training algorithm.
	 * @param Algorithm that needs to be used for creating trainer.
	 * @return  Instance of ClassifierTrainer that should be used for Training.
	 */
	@SuppressWarnings("unchecked")
	private ClassifierTrainer CreateTrainer (int trainingAlgo)  throws java.lang.Exception {
	
		ClassifierTrainer trainer = null;
		
		switch (trainingAlgo) {
		case NAIVEBAYES:
			trainer = MalletTrainerFactory.CreateNaiveBayesTrainer();
			break;
		case MAXENT:
			trainer = MalletTrainerFactory.CreateMaxEntTrainer();
			break;
		case DECISIONTREES:
			trainer = MalletTrainerFactory.CreateDecisionTreeTrainer();
			break;
		case C45:
			trainer = MalletTrainerFactory.CreateC45Trainer();
			break;
		case BALANCEDWINNOW:
			trainer = MalletTrainerFactory.CreateBalancedWinnowTrainer();
			break;
		case RANKMAXENT:
			trainer = MalletTrainerFactory.CreateRankMaxEntTrainer();
			break;
		case NAIVEBAYES_EM:
			trainer = MalletTrainerFactory.CreateNaiveBayesEMTrainer();
			break;
		case MAXENT_GE:
			trainer = MalletTrainerFactory.CreateMaxEntGETrainer();
			break;
		case MC_MAXENT:
			trainer = MalletTrainerFactory.CreateMCMAxEntTrainer();
			break;
		default:
			throw new Exception ("Unknown Trainer Algorithm");
		}
				
		return trainer;				
	}
		
	/**
	 * Creates Trainer on the specified algorithm and Trains it with data provided(as Instance Lists).
	 * 
	 * @param listToTrain				Data that needs to be trained.
	 * @param trainerAlgo				Algorithm that needs to be used for training.
	 * @return Instance of TrainerObject which contains the trainer that was trained and instance of classifier that was created by training of data.
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public TrainerObject train (InstanceList listToTrain, int trainerAlgo) throws NullPointerException {
	
		if (listToTrain == null)
			throw new NullPointerException ("Instance List to be trained is null");
		
		ClassifierTrainer trainer = null;
		try {
			trainer = this.CreateTrainer(trainerAlgo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Classifier cl = trainer.train(listToTrain);
		
		TrainerObject trnObject = new TrainerObject ();
		try {
			trnObject.setTrainerObject(trainer, cl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return trnObject;
	}

	/**
	 * Incrementally trains(updates) the trainer specified with the new set of data specified.
	 * 
	 * @param prevTrainer			Instance to Trainer that needs to be updated.
	 * @param listToTrain			New set of data with which the trainer specified needs to be trained.
	 * @return Instance to TrainerObject that contains the updated trainer and the classifier.
	 * @throws NullPointerException
	 */
	@SuppressWarnings("unchecked")
	public TrainerObject trainIncremental (ClassifierTrainer prevTrainer, 
											InstanceList listToTrain) throws NullPointerException {
		
		if (prevTrainer == null)
			throw new NullPointerException ("prev Trainer is null pointer");
		
		if (listToTrain == null)
			throw new NullPointerException ("Instance list to be trained is null pointer");
		
		int trainerAlgo = this.getTrainerAlgo(prevTrainer);
		
		Classifier cl = null;
		if (trainerAlgo == NAIVEBAYES)
		{
			NaiveBayesTrainer nbTrainer = (NaiveBayesTrainer) prevTrainer; 
			cl = nbTrainer.trainIncremental(listToTrain);
		}
		else
			throw new NullPointerException ("Trainer needs to be Naive bayes for incremental training");
		
		TrainerObject trnObject = new TrainerObject ();
		
		try {
			trnObject.setTrainerObject(prevTrainer, cl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return trnObject;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Encapsulates the trainer and classifier created in the process of Training the data on mallet classifiers.	 * 
	 */
	public class  TrainerObject {
		private Classifier classifier = null;		
		private ClassifierTrainer trainer = null;
				
		public TrainerObject () { }
		
		/**
		 * 
		 * @param trainerCreated	trainer that was created as a process of training the classifier.
		 * @param cl				Instance of classifier that was created due to training.
		 * @throws java.lang.Exception
		 */
		void setTrainerObject (ClassifierTrainer trainerCreated, Classifier cl) throws java.lang.Exception {
			
			if ( trainerCreated == null || cl == null)
				throw new Exception ("Trainer or Classifier to be set is null pointer");
			
			trainer = trainerCreated;
			classifier = cl;
		}
		
		/**
		 * 
		 * @return Instance of trainer that was saved.
		 */
		public ClassifierTrainer getTrainer () { return trainer; }
		
		/**
		 * 
		 * @return Instance of Classifier that was saved.
		 */
		public Classifier getClassifier () { return classifier; }	
	}
}


